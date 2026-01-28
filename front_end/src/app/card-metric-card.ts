import {Component, Input} from '@angular/core';

@Component({
  selector: 'app-metric-card',
  standalone: true,
  template: `
    <div class="card">
      <span class="label">{{ label }}</span>
      <div class="value">{{ value }}<small>{{ unit }}</small></div>
    </div>
  `,
  styles: [`
    .card { background: #f8fafc; padding: 16px 8px; border-radius: 16px; border: 1px solid #f1f5f9; }
    .label { font-size: 0.75rem; color: #64748b; display: block; margin-bottom: 4px; }
    .value { font-size: 1.2rem; font-weight: 700; color: #1e293b; }
  `]
})
export class MetricCardComponent {
  @Input() label!: string;
  @Input() value!: string | null;
  @Input() unit: string = '';
}
