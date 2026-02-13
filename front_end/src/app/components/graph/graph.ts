import {Component, effect, input, signal, ViewChild} from '@angular/core';
import {Chart, ChartConfiguration, ChartOptions, registerables} from 'chart.js';
import {GraphViewService} from '../../services/graphView/GraphViewService';
import {BaseChartDirective} from 'ng2-charts';
import {MeasuredValue} from './MeasuredValue';

@Component({
  selector: 'graph-chart-component',
  standalone: true,
  imports: [BaseChartDirective],
  template: `
    <div class="controls">
      <label>Timeframe: </label>
      <select (change)="updateHours($event)">
        <option value="6">6 Hours</option>
        <option value="12">12 Hours</option>
        <option value="24" selected>24 Hours</option>
        <option value="48">48 Hours</option>
      </select>
    </div>

    <div style="display: block; height: 300px; width: 100%;">
      <canvas baseChart
              [data]="lineChartData"
              [options]="lineChartOptions"
              [type]="'line'">
      </canvas>
    </div>
  `
})
export class GraphChartComponent {
  measuredValueInput = input.required<MeasuredValue>();
  selectedHours = signal<number>(24);

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
      const currentMeasuredValue = this.measuredValueInput();
      const hours = this.selectedHours();
      if (currentMeasuredValue) {
        this.loadData(currentMeasuredValue, hours);
      }
    });
  }

  updateHours(event: Event) {
    const value = (event.target as HTMLSelectElement).value;
    this.selectedHours.set(Number(value));
  }

  private loadData(measuredValue: MeasuredValue, hours: number) {
    this.graphViewService.getGraphData(measuredValue, hours).subscribe(graphView => {
      if (!graphView?.items?.length || !this.chart?.chart) {
        console.warn('No data received');
        return;
      }

      const labels = graphView.items.map(item =>
        new Date(item.time).toLocaleTimeString('de-CH', {
          day: '2-digit',
          month: '2-digit',
          hour: '2-digit',
          minute: '2-digit' })
      );
      const dataPoints = graphView.items.map(item => item.value);

      this.lineChartData = {
        labels: labels,
        datasets: [{
          ...this.lineChartData.datasets[0],
          label: graphView.nameWithUnit || 'Measurement',
          data: dataPoints
        }]
      };

      const chartInstance = this.chart.chart;
      chartInstance.data.labels = labels;
      chartInstance.data.datasets[0].data = dataPoints;
      chartInstance.update();
    });
  }
}
