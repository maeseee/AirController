import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {ClimateDataPoint} from './climate-data-point';

@Injectable({
  providedIn: 'root',
})
export class CurrentIndoorClimateDataPointService {
  private readonly API_URL = 'http://192.168.50.12:9090/currentState/indoorClimateDataPoint';

  constructor(private http: HttpClient) {}

  getDataPoint(): Observable<ClimateDataPoint> {
    return this.http.get<ClimateDataPoint>(this.API_URL, { withCredentials: true });
  }
}
