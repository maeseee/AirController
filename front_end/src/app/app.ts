import {Component, OnInit} from '@angular/core';
import {CommonModule, NgOptimizedImage} from '@angular/common';
import {freshAirStatus} from './fresh-air-status.service';
import {ClimateDataPoint} from './climate-data-point';
import {CurrentIndoorClimateDataPointService} from './current-indoor-climate-data-point.service';
import {FreshAirConfidences} from './fresh-air-confidences';
import {CurrentTotalConfidence} from './current-total-confidence';
import {ConfidenceMap} from './confidence-map';
import {catchError, forkJoin, Observable, of} from 'rxjs';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, NgOptimizedImage],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  viewModel$?: Observable<{
    status: string;
    dataPoint: ClimateDataPoint;
    totalConfidence: number;
    confidences: ConfidenceMap;
  }>;

  constructor(
    private airService: freshAirStatus,
    private climateDataPoint: CurrentIndoorClimateDataPointService,
    private freshAirTotalConfidence: CurrentTotalConfidence,
    private freshAirConfidences: FreshAirConfidences) {
  }

  ngOnInit() {
    this.refresh();
  }

  refresh() {
    this.viewModel$ = forkJoin({
      status: this.airService.getStatus().pipe(catchError(() => of('ERROR (Check Java/CORS)'))),
      dataPoint: this.climateDataPoint.getDataPoint(),
      totalConfidence: this.freshAirTotalConfidence.getTotalConfidence(),
      confidences: this.freshAirConfidences.getConfidences()
    }).pipe(
      catchError(err => {
        console.error('Batch update failed', err);
        throw err;
      })
    );
  }

  protected readonly Object = Object;
}
