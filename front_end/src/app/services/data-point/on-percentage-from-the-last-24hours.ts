import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class OnPercentageFromTheLast24Hours {
  private http = inject(HttpClient);
  private readonly URL = 'http://192.168.50.12:9090/statistics/onPercentageFromTheLast24Hours';

  getPercentage(): Observable<number> {
    return this.http.get<number>(this.URL, {withCredentials: true});
  }
}
