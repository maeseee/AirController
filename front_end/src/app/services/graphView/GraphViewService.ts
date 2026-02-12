import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {GraphView} from './GraphView';
import {MeasuredValue} from '../../components/graph/MeasuredValue';

@Injectable({
  providedIn: 'root',
})
export class GraphViewService {
  private http = inject(HttpClient);
  private readonly BASE_URL = 'http://192.168.50.12:9090/graph';
  public location: 'indoor' | 'outdoor' = 'indoor';

  getGraphData(measuredValue: MeasuredValue, hours: number): Observable<GraphView> {
    const url = `${this.BASE_URL}/${this.location}/${measuredValue}/${hours}`;
    return this.http.get<GraphView>(url, {withCredentials: true});
  }
}
