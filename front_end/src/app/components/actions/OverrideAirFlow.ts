import { Component } from '@angular/core';
import {OverrideAirFlowService} from '../../services/actions/OverrideAirFlowService';

@Component({
  selector: 'override-airflow-state',
  template: `
    <button class="refresh-btn" (click)="setOn()">On 15m</button>
    <button class="refresh-btn" (click)="setOff()">Off 15m</button>
  `
})
export class AirflowControlComponent {
  constructor(private service: OverrideAirFlowService) {}

  setOn() {
    this.service.setOn(15).subscribe({
      next: () => console.log('notified on'),
      error: (err: any) => console.error(err)
    });
  }

  setOff() {
    this.service.setOff(15).subscribe({
      next: () => console.log('notified off'),
      error: (err: any) => console.error(err)
    });
  }
}
