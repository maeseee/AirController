import {Component, inject, signal} from '@angular/core';
import {CommonModule, NgOptimizedImage} from '@angular/common';
import {toObservable, toSignal} from '@angular/core/rxjs-interop';
import {BehaviorSubject, catchError, combineLatest, forkJoin, of, switchMap} from 'rxjs';

import {freshAirStatus} from './services/system-status/fresh-air-status';
import {MetricCardComponent} from './components/card/metric-card';
import {CardViewService} from './services/cardView/CardViewService';
import {GraphChartComponent} from './components/graph/graph';
import {MeasuredValue} from './components/graph/MeasuredValue';

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

  private refresh$ = new BehaviorSubject<void>(void 0);

  indoorGraphProfile = signal<MeasuredValue | null>(null);

  private data$ = combineLatest([
    this.refresh$,
    toObservable(this.indoorGraphProfile)
  ]).pipe(
    switchMap(() => forkJoin({
      status: this.airService.getStatus().pipe(catchError(() => of('ERROR'))),
      indoorCardViews: this.cardViewService.getCardViews('indoor'),
      outdoorCardViews: this.cardViewService.getCardViews('outdoor'),
      confidenceCardViews: this.cardViewService.getCardViews('confidence'),
      statisticsCardViews: this.cardViewService.getCardViews('statistics')
    })),
    catchError(err => {
      console.error('Batch update failed', err);
      return of(null);
    })
  );

  viewModel = toSignal(this.data$, {initialValue: null});

  refresh() {
    this.refresh$.next();
  }

  setIndoorGraphProfile(profile: string) {
    const profileString = profile.toUpperCase() as any;
    const value = (MeasuredValue as any)[profileString] as MeasuredValue;
    if (this.indoorGraphProfile() === value) {
      this.indoorGraphProfile.set(null);
    } else {
      this.indoorGraphProfile.set(value);
    }
  }
}
