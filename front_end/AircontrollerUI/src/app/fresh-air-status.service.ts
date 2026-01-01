import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class FreshAirStatus {
  // Different containers: 'http://192.168.50.12:8080/currentState/freshAir'
  private readonly API_URL = '/api/currentState/freshAir'; // With Nginx-bridge (proxy)

  constructor(private http: HttpClient) {}

  getStatus(): Observable<string> {
    return this.http.get(this.API_URL, { responseType: 'text' });
  }
}
