import {Component, EventEmitter, Input, Output} from '@angular/core';
import {GraphChartComponent} from '../graph/graph';
import {CommonModule} from '@angular/common';
import {MetricCardComponent} from '../card/metric-card';

@Component({
  selector: 'app-dashboard-section',
  standalone: true,
  imports: [GraphChartComponent, CommonModule, MetricCardComponent],
  templateUrl: './metric-dashboard.html',
  styleUrl: './metric-dashboard.css'
})
export class DashboardSectionComponent {
  @Input({required: true}) title!: string;
  @Input() info?: string;
  @Input() cards: any[] = [];
  @Input() location: 'system' | 'indoor' | 'outdoor' = 'system';
  @Input() activeGraphMetric: any = null;

  @Output() cardSelected = new EventEmitter<string>();

  onCardClick(name: string) {
    this.cardSelected.emit(name);
  }
}
