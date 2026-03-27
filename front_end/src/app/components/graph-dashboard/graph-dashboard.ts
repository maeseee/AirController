import {Component, EventEmitter, Input, Output, signal} from '@angular/core';
import {GraphChartComponent} from '../graph/graph';
import {CommonModule} from '@angular/common';
import {MetricCardComponent} from '../metric-card/metric-card';

@Component({
  selector: 'app-dashboard-section',
  standalone: true,
  imports: [GraphChartComponent, CommonModule, MetricCardComponent],
  templateUrl: './graph-dashboard.html',
  styleUrl: './graph-dashboard.css'
})
export class DashboardSectionComponent {
  @Input({required: true}) title!: string;
  @Input() info?: string;
  @Input() cards: any[] = [];
  @Input() location: 'system' | 'indoor' | 'outdoor' = 'system';
  @Input() activeGraphMetric: any = null;

  selectedHours = signal<number>(24);
  isMaximized = false;


  @Output() cardSelected = new EventEmitter<string>();

  onCardClick(name: string) {
    this.cardSelected.emit(name);
  }

  updateHours(event: Event) {
    const value = (event.target as HTMLSelectElement).value;
    this.selectedHours.set(Number(value));
  }

  toggleZoom() {
    this.isMaximized = !this.isMaximized;
  }
}
