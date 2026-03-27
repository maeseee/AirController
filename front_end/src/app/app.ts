import {Component, inject, signal} from '@angular/core';
import {CommonModule, NgOptimizedImage} from '@angular/common';
import {toSignal} from '@angular/core/rxjs-interop';
import {BehaviorSubject, catchError, forkJoin, of, switchMap, tap } from 'rxjs';
import {CardViewService} from './services/cardView/CardViewService';
import {MeasuredValue} from './components/graph/MeasuredValue';
import {AirflowControlComponent} from './components/actions/OverrideAirFlow';
import {DashboardSectionComponent} from './components/graph-dashboard/graph-dashboard';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, NgOptimizedImage, AirflowControlComponent, DashboardSectionComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  private cardViewService = inject(CardViewService);

  private refresh$ = new BehaviorSubject<void>(void 0);

  systemGraphProfile = signal<'airflow' | null>(null);
  indoorGraphProfile = signal<MeasuredValue | null>(null);
  outdoorGraphProfile = signal<MeasuredValue | null>(null);
  hasError = signal<boolean>(false);

  private data$ = this.refresh$.pipe(
    tap(() => this.hasError.set(false)),
    switchMap(() => forkJoin({
      systemCardViews: this.cardViewService.getCardViews('system'),
      indoorCardViews: this.cardViewService.getCardViews('indoor'),
      outdoorCardViews: this.cardViewService.getCardViews('outdoor'),
      confidenceCardViews: this.cardViewService.getCardViews('confidence'),
      statisticsCardViews: this.cardViewService.getCardViews('statistics')
    })),
    catchError(err => {
      console.error('Batch update failed', err);
      this.hasError.set(true);
      return of(null);
    })
  );

  viewModel = toSignal(this.data$, {initialValue: null});

  refresh() {
    this.refresh$.next();
  }

  setSystemGraphProfile(profile: 'airflow' | null) {
    if (this.systemGraphProfile() === profile) {
      this.systemGraphProfile.set(null);
    } else {
      this.systemGraphProfile.set(profile);
    }
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

  setOutdoorGraphProfile(profile: string) {
    const profileString = profile.toUpperCase() as any;
    const value = (MeasuredValue as any)[profileString] as MeasuredValue;
    if (this.outdoorGraphProfile() === value) {
      this.outdoorGraphProfile.set(null);
    } else {
      this.outdoorGraphProfile.set(value);
    }
  }
}
