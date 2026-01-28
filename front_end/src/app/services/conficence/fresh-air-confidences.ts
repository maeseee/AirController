import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {ConfidenceMap} from './confidence-map';

@Injectable({
  providedIn: 'root',
})
export class FreshAirConfidences {
  private readonly API_URL = 'http://192.168.50.12:9090/currentState/confidences';

  constructor(private http: HttpClient) {}

  getConfidences(): Observable<ConfidenceMap> {
    return this.http.get<ConfidenceMap>(this.API_URL, { withCredentials: true });
  }
}
