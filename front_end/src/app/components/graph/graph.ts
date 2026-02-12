import {Component, effect, input, ViewChild} from '@angular/core';
import {Chart, ChartConfiguration, ChartOptions, registerables} from 'chart.js';
import {GraphViewService} from '../../services/graphView/GraphViewService';
import {BaseChartDirective} from 'ng2-charts';
import {MeasuredValue} from './MeasuredValue';

@Component({
  selector: 'graph-chart-component',
  standalone: true,
  imports: [BaseChartDirective],
  template: `
    <div style="display: block; height: 100%; width: 100%;">
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
      if (currentMeasuredValue) {
        this.loadData(currentMeasuredValue, 24);
      }
    });
  }

  private loadData(measuredValue: MeasuredValue, hours: number) {
    this.graphViewService.getGraphData(measuredValue, hours).subscribe(graphView => {
      if (!graphView?.items?.length) {
        console.warn('No data received');
        return;
      }

      this.lineChartData = {
        labels: graphView.items.map(item =>
          new Date(item.time).toLocaleTimeString([], {hour: '2-digit', minute: '2-digit'})
        ),
        datasets: [{
          label: graphView.nameWithUnit || 'Measurement',
          data: graphView.items.map(item => item.value),
          borderColor: '#ff6384',
          backgroundColor: 'rgba(255, 99, 132, 0.2)',
          tension: 0.3,
          pointRadius: 2
        }]
      };

      this.chart?.update();
    });
  }
}
