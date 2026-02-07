import {Component, OnInit, ViewChild} from '@angular/core';
import {Chart, ChartConfiguration, ChartOptions, registerables} from 'chart.js';
import {GraphViewService} from '../../services/graphView/GraphViewService';
import {BaseChartDirective} from 'ng2-charts';

@Component({
  selector: 'graph-chart-component',
  standalone: true,
  imports: [BaseChartDirective],
  template: `
    <div style="display: block; height: 400px; width: 100%;">
      <canvas baseChart
              [data]="lineChartData"
              [options]="lineChartOptions"
              [type]="'line'">
      </canvas>
    </div>
  `
})
export class GraphChartComponent implements OnInit {

  @ViewChild(BaseChartDirective) chart?: BaseChartDirective;

  public lineChartData: ChartConfiguration<'line'>['data'] = {
    labels: [],
    datasets: []
  };

  public lineChartOptions: ChartOptions<'line'> = {
    responsive: true,
    maintainAspectRatio: true,
    scales: {
      yAxisConfig: {
        type: 'linear',
        display: true,
        position: 'left',
      }
    },
    plugins: {
      legend: {display: true}
    }
  };

  constructor(private graphViewService: GraphViewService) {
    Chart.register(...registerables);
  }

  ngOnInit() {
    this.graphViewService.getGraphData('indoortemperature').subscribe(graphView => {
      if (!graphView || !graphView.items || graphView.items.length === 0) {
        console.warn('No data received');
        return;
      }

      this.lineChartData = {
        labels: graphView.items.map(item => {
          // Optional: Hier den ISO-String kürzen für bessere Lesbarkeit
          // Beispiel: "2026-02-06T21:00:00" -> "21:00"
          const date = new Date(item.time);
          return date.toLocaleTimeString([], {hour: '2-digit', minute: '2-digit'});
        }),
        datasets: [
          {
            label: graphView.nameWithUnit || 'Temperatur',
            data: graphView.items.map(item => item.value),
            borderColor: '#ff6384',
            backgroundColor: 'rgba(255, 99, 132, 0.2)',
            yAxisID: 'yAxisConfig',
            fill: false,
            tension: 0.3,
            pointRadius: 2
          }
        ]
      };

      setTimeout(() => {
        if (this.chart) {
          this.chart.update();
        }
      }, 0);
    });
  }
}
