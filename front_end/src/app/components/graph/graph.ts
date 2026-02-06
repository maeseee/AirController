import {Component, OnInit, ViewChild} from '@angular/core';
import {Chart, ChartConfiguration, ChartOptions, registerables} from 'chart.js';
import {GraphViewService} from '../../services/graphView/GraphViewService';
import {BaseChartDirective} from 'ng2-charts';

@Component({
  selector: 'graph-chart-component',
  standalone: true, // Falls du Angular 15+ nutzt
  imports: [BaseChartDirective], // Wichtig für ng2-charts
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
    datasets: [
      {
        data: [],
        label: 'Temperature (°C)',
        borderColor: '#ff6384',
        backgroundColor: 'rgba(255, 99, 132, 0.2)',
        fill: true,
      }
    ]
  };

  public lineChartOptions: ChartOptions<'line'> = {
    responsive: true,
    scales: {
      // Hier müssen die IDs stehen, die getYAxisId() liefert!
      yTemp: {
        type: 'linear',
        display: true,
        position: 'left',
        title: { display: true, text: 'Temperatur (°C)' }
      }
    }
  };

  constructor(private graphViewService: GraphViewService) {
    Chart.register(...registerables);
  }

  ngOnInit() {
    this.graphViewService.getGraphData('indoortemperature').subscribe(graphView => {
      console.log('API Response:', graphView.items);
      if (graphView.items.length === 0) {
        console.log('Exit');
        return;
      }

      this.lineChartData = {
        labels: graphView.items.map(item => item.timestamp),
        datasets: [
          {
            label: graphView.nameWithUnit,
            data: graphView.items.map(item => item.value),
            borderColor: '#ff6384',
            backgroundColor: 'rgba(255, 99, 132, 0.2)',
            yAxisID: this.getYAxisId(graphView.nameWithUnit),
            tension: 0.3
          }
        ]
      };

      // WICHTIG: Falls der Chart sich nicht automatisch aktualisiert
      setTimeout(() => this.chart?.update(), 50);
      this.chart?.update();
    });
  }

// Hilfsmethode für die Achsen-Zuordnung
  private getYAxisId(name: string): string {
    if (name.includes('°C')) return 'yTemp';
    if (name.includes('%')) return 'yHumidity';
    if (name.includes('hPa')) return 'yPressure';
    return 'yDefault';
  }
}
