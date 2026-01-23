import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class CurrentTotalConfidence {
  private readonly API_URL = 'http://192.168.50.12:9090/currentState/total_confidence';

  constructor(private http: HttpClient) {}

  getTotalConfidence(): Observable<number> {
    return this.http.get<number>(this.API_URL, { withCredentials: true });
  }
}
