import {Component, computed, inject} from '@angular/core';
import {CommonModule, NgOptimizedImage} from '@angular/common';
import {toSignal} from '@angular/core/rxjs-interop';
import {BehaviorSubject, catchError, forkJoin, of, switchMap} from 'rxjs';

import {freshAirStatus} from './fresh-air-status.service';
import {CurrentClimateDataPointService} from './current-climate-data-point.service';
import {CurrentTotalConfidence} from './current-total-confidence';
import {FreshAirConfidences} from './fresh-air-confidences';
import {MetricCardComponent} from './components/metric-card/metric-card';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, NgOptimizedImage, MetricCardComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  private airService = inject(freshAirStatus);
  private dataPointService = inject(CurrentClimateDataPointService);
  private totalConfService = inject(CurrentTotalConfidence);
  private confidencesService = inject(FreshAirConfidences);

  private refresh$ = new BehaviorSubject<void>(void 0);

  private data$ = this.refresh$.pipe(
    switchMap(() => forkJoin({
      status: this.airService.getStatus().pipe(catchError(() => of('ERROR'))),
      indoorDataPoint: this.dataPointService.getDataPoint('indoor'),
      outdoorDataPoint: this.dataPointService.getDataPoint('outdoor'),
      totalConfidence: this.totalConfService.getTotalConfidence(),
      confidences: this.confidencesService.getConfidences()
    })),
    catchError(err => {
      console.error('Batch update failed', err);
      return of(null); // Return null on total failure
    })
  );

  viewModel = toSignal(this.data$, {initialValue: null});

  confidencesArray = computed(() => {
    const data = this.viewModel();
    if (!data?.confidences) return [];
    return Object.entries(data.confidences).map(([key, value]) => ({key, value}));
  });

  refresh() {
    this.refresh$.next();
  }
}
