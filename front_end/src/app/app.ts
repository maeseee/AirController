import {Component, OnInit} from '@angular/core';
import {CommonModule, NgOptimizedImage} from '@angular/common';
import {freshAirStatus} from './fresh-air-status.service';
import {ClimateDataPoint} from './climate-data-point';
import {CurrentIndoorClimateDataPointService} from './current-indoor-climate-data-point.service';
import {CurrentOutdoorClimateDataPointService} from './current-outdoor-climate-data-point.service';
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
    indoorDataPoint: ClimateDataPoint;
    outdoorDataPoint: ClimateDataPoint;
    totalConfidence: number;
    confidences: ConfidenceMap;
  }>;

  constructor(
    private airService: freshAirStatus,
    private indoorClimateDataPoint: CurrentIndoorClimateDataPointService,
    private outdoorClimateDataPoint: CurrentOutdoorClimateDataPointService,
    private freshAirTotalConfidence: CurrentTotalConfidence,
    private freshAirConfidences: FreshAirConfidences) {
  }

  ngOnInit() {
    this.refresh();
  }

  refresh() {
    this.viewModel$ = forkJoin({
      status: this.airService.getStatus().pipe(catchError(() => of('ERROR (Check Java/CORS)'))),
      indoorDataPoint: this.indoorClimateDataPoint.getDataPoint(),
      outdoorDataPoint: this.outdoorClimateDataPoint.getDataPoint(),
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
