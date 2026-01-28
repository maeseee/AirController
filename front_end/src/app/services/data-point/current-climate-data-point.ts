import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {ClimateDataPoint} from './climate-data-point';

@Injectable({
  providedIn: 'root',
})
export class CurrentClimateDataPoint {
  private http = inject(HttpClient);
  private readonly BASE_URL = 'http://192.168.50.12:9090/currentState';

  getDataPoint(location: 'indoor' | 'outdoor'): Observable<ClimateDataPoint> {
    const url = `${this.BASE_URL}/${location}ClimateDataPoint`;
    return this.http.get<ClimateDataPoint>(url, {withCredentials: true});
  }
}
