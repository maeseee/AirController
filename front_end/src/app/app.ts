import {Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {freshAirStatus} from './fresh-air-status.service';
import {ClimateDataPoint} from './climate-data-point';
import {CurrentClimateDataPointService} from './current-climate-data-point.service';
import {FreshAirConfidences} from './fresh-air-confidences';
import {CurrentTotalConfidence} from './current-total-confidence';
import {ConfidenceMap} from './confidence-map';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  airStatus: string = 'LOADING...';
  dataPoint?: ClimateDataPoint;
  totalConfidence?: number;
  confidences: ConfidenceMap = {};

  constructor(
    private airService: freshAirStatus,
    private climateDataPoint: CurrentClimateDataPointService,
    private freshAirTotalConfidence: CurrentTotalConfidence,
    private freshAirConfidences: FreshAirConfidences) {
  }

  ngOnInit() {
    this.refresh();
  }

  refresh() {
    this.airService.getStatus().subscribe({
      next: (val) => this.airStatus = val,
      error: () => this.airStatus = 'ERROR (Check Java/CORS)'
    });
    this.climateDataPoint.getDataPoint().subscribe({
      next: (dataPoint) => this.dataPoint = dataPoint,
      error: (err) => console.error('Connection failed', err)
    });
    this.freshAirTotalConfidence.getTotalConfidence().subscribe({
      next: (totalConfidence) => this.totalConfidence = totalConfidence,
      error: (err) => console.error('Connection failed', err)
    });
    this.freshAirConfidences.getConfidences().subscribe({
      next: (confidences) => this.confidences = confidences,
      error: (err) => console.error('Connection failed', err)
    });
  }

  protected readonly Object = Object;
}
