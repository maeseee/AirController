import {Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FreshAirStatus} from './fresh-air-status.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  airStatus: string = 'LOADING...';

  constructor(private airService: FreshAirStatus) {
  }

  ngOnInit() {
    this.refresh();
  }

  refresh() {
    this.airService.getStatus().subscribe({
      next: (val) => this.airStatus = val,
      error: (err) => this.airStatus = 'ERROR (Check Java/CORS)'
    });
  }
}
