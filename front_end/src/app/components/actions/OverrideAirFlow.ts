import { Component } from '@angular/core';
import { OverrideAirFlowService } from '../../services/actions/OverrideAirFlowService';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'override-airflow-state',
  standalone: true,
  imports: [FormsModule],
  template: `
    <div class="override-row">
      <select class="duration-select" [(ngModel)]="duration">
        @for (d of durations; track d.value) {
          <option [ngValue]="d.value">{{ d.label }}</option>
        }
      </select>

      <button
        class="override-btn override-on"
        (click)="setOn(duration)"
        title="Turn on for selected duration">
        <span class="override-btn__action">On</span>
      </button>

      <button
        class="override-btn override-off"
        (click)="setOff(duration)"
        title="Turn off for selected duration">
        <span class="override-btn__action">Off</span>
      </button>
    </div>
  `,
  styleUrls: ['../../app.css']
})
export class AirflowControlComponent {
  durations = [
    { value: 5, label: '5m' },
    { value: 15, label: '15m' },
    { value: 30, label: '30m' },
    { value: 60, label: '1h' },
    { value: 120, label: '2h' }
  ];

  duration = 15;

  constructor(private service: OverrideAirFlowService) {}

  setOn(minutes: number) {
    this.service.setOn(minutes).subscribe({
      next: () => console.log('notified on'),
      error: (err: any) => console.error(err)
    });
  }

  setOff(minutes: number) {
    this.service.setOff(minutes).subscribe({
      next: () => console.log('notified off'),
      error: (err: any) => console.error(err)
    });
  }
}
