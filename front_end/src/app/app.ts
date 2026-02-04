import {Component, inject} from '@angular/core';
import {CommonModule, NgOptimizedImage} from '@angular/common';
import {toSignal} from '@angular/core/rxjs-interop';
import {BehaviorSubject, catchError, forkJoin, of, switchMap} from 'rxjs';

import {freshAirStatus} from './services/system-status/fresh-air-status';
import {MetricCardComponent} from './components/metric-card/metric-card';
import {CardViewService} from './services/cardView/CardViewService';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, NgOptimizedImage, MetricCardComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  private airService = inject(freshAirStatus);
  private cardViewService = inject(CardViewService);

  private refresh$ = new BehaviorSubject<void>(void 0);

  private data$ = this.refresh$.pipe(
    switchMap(() => forkJoin({
      status: this.airService.getStatus().pipe(catchError(() => of('ERROR'))),
      indoorCardViews: this.cardViewService.getCardViews('indoor'),
      outdoorCardViews: this.cardViewService.getCardViews('outdoor'),
      confidenceCardViews: this.cardViewService.getCardViews('confidence'),
      statisticsCardViews: this.cardViewService.getCardViews('statistics')
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
}
