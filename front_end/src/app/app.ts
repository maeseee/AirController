import {Component, computed, inject} from '@angular/core';
import {CommonModule, NgOptimizedImage} from '@angular/common';
import {toSignal} from '@angular/core/rxjs-interop';
import {BehaviorSubject, catchError, forkJoin, of, switchMap} from 'rxjs';

import {freshAirStatus} from './services/system-status/fresh-air-status';
import {CurrentTotalConfidence} from './services/conficence/current-total-confidence';
import {FreshAirConfidences} from './services/conficence/fresh-air-confidences';
import {MetricCardComponent} from './components/metric-card/metric-card';
import {OnPercentageFromTheLast24Hours} from './services/data-point/on-percentage-from-the-last-24hours';
import {CardGroupService} from './services/cardView/CardGroupService';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, NgOptimizedImage, MetricCardComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  private airService = inject(freshAirStatus);
  private cardGroupService = inject(CardGroupService);
  private totalConfService = inject(CurrentTotalConfidence);
  private confidencesService = inject(FreshAirConfidences);
  private onPercentageService = inject(OnPercentageFromTheLast24Hours);

  private refresh$ = new BehaviorSubject<void>(void 0);

  private data$ = this.refresh$.pipe(
    switchMap(() => forkJoin({
      status: this.airService.getStatus().pipe(catchError(() => of('ERROR'))),
      indoorCardViews: this.cardGroupService.getCardViews('indoor'),
      outdoorCardViews: this.cardGroupService.getCardViews('outdoor'),
      totalConfidence: this.totalConfService.getTotalConfidence(),
      confidences: this.confidencesService.getConfidences(),
      confidenceCardViews: this.cardGroupService.getCardViews('confidence'),
      onPercentage: this.onPercentageService.getPercentage()
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
