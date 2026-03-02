import {Component, Input} from '@angular/core';

@Component({
  selector: 'app-metric-card',
  standalone: true,
  templateUrl: './metric-card.html',
  styleUrls: ['./metric-card.css']
})
export class MetricCardComponent {
  @Input() label!: string;
  @Input() value!: string | null;
  @Input() unit: string = '';
}
