import {Component, effect, input, signal, ViewChild} from '@angular/core';
import {Chart, ChartConfiguration, ChartOptions, registerables} from 'chart.js';
import {GraphViewService} from '../../services/graphView/GraphViewService';
import {BaseChartDirective} from 'ng2-charts';
import {MeasuredValue} from './MeasuredValue';

@Component({
  selector: 'graph-chart-component',
  standalone: true,
  imports: [BaseChartDirective],
  templateUrl: './graph.html',
})
export class GraphChartComponent {
  location = input.required<'system' | 'indoor' | 'outdoor'>();
  selectedHours = signal<number>(24);
  measuredValueInput = input.required<'airflow' | MeasuredValue>();

  @ViewChild(BaseChartDirective) chart?: BaseChartDirective;

  public lineChartData: ChartConfiguration<'line'>['data'] = {
    labels: [],
    datasets: []
  };

  public lineChartOptions: ChartOptions<'line'> = {
    responsive: true,
    maintainAspectRatio: true,
    locale: 'de-CH',
    scales: {
      y: {
        type: 'linear',
        display: true,
        position: 'left'
      }
    },
    plugins: {
      legend: {display: true}
    }
  };

  constructor(private graphViewService: GraphViewService) {
    Chart.register(...registerables);
    effect(() => {
      const location = this.location();
      const currentMeasuredValue = this.measuredValueInput();
      const hours = this.selectedHours();
      if (currentMeasuredValue) {
        this.loadData(location, currentMeasuredValue, hours);
      }
    });
  }

  updateHours(event: Event) {
    const value = (event.target as HTMLSelectElement).value;
    this.selectedHours.set(Number(value));
  }

  private loadData(location: 'system' | 'indoor' | 'outdoor', measuredValue: 'airflow' | MeasuredValue, hours: number) {
    this.graphViewService.getGraphData(location, measuredValue, hours).subscribe(graphView => {
      if (!graphView?.items?.length) {
        console.warn('No data received');
        return;
      }

      const labels = graphView.items.map(item =>
        new Date(item.time).toLocaleTimeString('de-CH', {
          day: '2-digit',
          month: '2-digit',
          hour: '2-digit',
          minute: '2-digit'
        })
      );
      const dataPoints = graphView.items.map(item => item.value);

      this.lineChartData = {
        labels: labels,
        datasets: [
          {
            label: graphView.nameWithUnit || 'Measurement',
            data: dataPoints,
            borderColor: 'rgb(75, 192, 192)',
            tension: 0.1
          }
        ]
      };

      if (this.chart?.chart) {
        this.chart.chart.data = this.lineChartData;
        this.chart.chart.update();
      }
    });
  }
}
