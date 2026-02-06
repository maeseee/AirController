import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {GraphView} from './GraphView';

@Injectable({
  providedIn: 'root',
})
export class GraphViewService {
  private http = inject(HttpClient);
  private readonly BASE_URL = 'http://192.168.50.12:9090/graph';

  getGraphData(location: 'indoortemperature' | 'outdoortemperature'): Observable<GraphView> {
    const url = `${this.BASE_URL}/${location}`;
    return this.http.get<GraphView>(url, {withCredentials: true});
  }
}
