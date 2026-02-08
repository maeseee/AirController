import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {GraphView} from './GraphView';

export type MetricType = (typeof VALID_METRICS)[number];
export const VALID_METRICS = ['temperature', 'humidity', 'co2'] as const;

@Injectable({
  providedIn: 'root',
})
export class GraphViewService {
  private http = inject(HttpClient);
  private readonly BASE_URL = 'http://192.168.50.12:9090/graph';
  public location: 'indoor' | 'outdoor' = 'indoor';

  getGraphData(type: MetricType): Observable<GraphView> {
    const url = `${this.BASE_URL}/${this.location}${type}`;
    return this.http.get<GraphView>(url, {withCredentials: true});
  }
}
