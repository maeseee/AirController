import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class OverrideAirFlowService {
  private base = 'http://192.168.50.12:9090/action/airflow';

  constructor(private http: HttpClient) {}

  setOn(minutes: number) {
    return this.http.post(`${this.base}/on/${minutes}`, null);
  }

  setOff(minutes: number) {
    return this.http.post(`${this.base}/off/${minutes}`, null);
  }
}
