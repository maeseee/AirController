import {Component, inject, signal} from '@angular/core';
import {CommonModule, NgOptimizedImage} from '@angular/common';
import {toSignal} from '@angular/core/rxjs-interop';
import {BehaviorSubject, catchError, forkJoin, of, switchMap} from 'rxjs';

import {freshAirStatus} from './services/system-status/fresh-air-status';
import {MetricCardComponent} from './components/card/metric-card';
import {CardViewService} from './services/cardView/CardViewService';
import {GraphChartComponent} from './components/graph/graph';
import {GraphViewService, MetricType, VALID_METRICS} from './services/graphView/GraphViewService';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, NgOptimizedImage, MetricCardComponent, GraphChartComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  private airService = inject(freshAirStatus);
  private cardViewService = inject(CardViewService);
  private graphViewService = inject(GraphViewService);

  private refresh$ = new BehaviorSubject<void>(void 0);

  indoorGraphProfile = signal<MetricType | null>(null);

  private data$ = this.refresh$.pipe(
    switchMap(() => forkJoin({
      status: this.airService.getStatus().pipe(catchError(() => of('ERROR'))),
      indoorCardViews: this.cardViewService.getCardViews('indoor'),
      outdoorCardViews: this.cardViewService.getCardViews('outdoor'),
      confidenceCardViews: this.cardViewService.getCardViews('confidence'),
      statisticsCardViews: this.cardViewService.getCardViews('statistics'),
      temperatureGraphView: this.graphViewService.getGraphData('temperature')
    })),
    catchError(err => {
      console.error('Batch update failed', err);
      return of(null); // Return null on total failure
    })
  );

  viewModel = toSignal(this.data$, {initialValue: null});

  refresh() {
    this.refresh$.next();
  }

  setIndoorGraphProfile(profile: string) {
    const profileString = profile.toLowerCase() as any;
    if (!VALID_METRICS.includes(profileString)) {
      console.error(`${profile} is not a valid metric!`);
    }
    if (this.indoorGraphProfile() === profileString) {
      this.indoorGraphProfile.set(null);
    } else {
      this.indoorGraphProfile.set(profileString);
    }
    this.refresh();
  }
}
