import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {CardView} from './CardView';

@Injectable({
  providedIn: 'root',
})
export class CardViewService {
  private http = inject(HttpClient);
  private readonly BASE_URL = 'http://192.168.50.12:9090/cardViews';

  getCardViews(location: 'indoor' | 'outdoor' | 'confidence' | 'statistics'): Observable<CardView> {
    const url = `${this.BASE_URL}/${location}`;
    return this.http.get<CardView>(url, {withCredentials: true});
  }
}
