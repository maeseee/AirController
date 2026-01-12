import {Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {freshAirStatus} from './fresh-air-status.service';
import {ClimateDataPoint} from './climate-data-point';
import {CurrentClimateDataPointService} from './current-climate-data-point.service';

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

  constructor(private airService: freshAirStatus, private climateDataPoint: CurrentClimateDataPointService) {
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
      next: (response) => this.dataPoint = response,
      error: (err) => console.error('Connection failed', err)
    });
  }
}
