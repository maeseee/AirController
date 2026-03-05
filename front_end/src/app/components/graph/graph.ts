import {Component, effect, input, signal, ViewChild} from '@angular/core';
import {Chart, ChartConfiguration, ChartOptions, registerables} from 'chart.js';
import {GraphViewService} from '../../services/graphView/GraphViewService';
import {BaseChartDirective} from 'ng2-charts';
import {MeasuredValue} from './MeasuredValue';
import 'chartjs-adapter-date-fns';
import {TimeScale, LinearScale, PointElement, LineElement, LineController, Tooltip, Legend} from 'chart.js';
import {de} from 'date-fns/locale';

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
    scales: {
      x: {
        type: 'time',
        time: {
          unit: 'hour',
          displayFormats: {
            hour: 'dd.MM. HH:mm'
          }
        },
        adapters: {
          date: {
            locale: de
          }
        },
        title: {
          display: true,
          text: 'Time'
        }
      },
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

      const dataPoints = graphView.items.map(item => ({
        x: new Date(item.time).getTime(),
        y: item.value
      }));

      this.lineChartData = {
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
