import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';// RxJS Observable type used by HttpClient
import { UserInfo } from '../models/user-info';// TypeScript interface representing user data returned from backend

@Injectable({ providedIn: 'root' })
// Angular-specific:
// - Marks this class as injectable
// - providedIn: 'root' makes it a singleton service for the whole app
export class UserService {
  private baseUrl = 'http://localhost:8080/api/users';

  // Angular DI injects HttpClient automatically
  constructor(private http: HttpClient) {}

  /**
   * Remote search API call.
   * Sends orgId, query text, and result limit to backend.
   *
   * Returns:
   * - Observable<UserInfo[]> that emits the matched users.
   */
  search(orgId: number, q: string, limit = 20): Observable<UserInfo[]> {

    // Build query parameters in a type-safe way
    const params = new HttpParams()
      .set('orgId', String(orgId))
      .set('q', q)
      .set('limit', String(limit));

    // The generic type <UserInfo[]> tells Angular how to deserialize JSON
    return this.http.get<UserInfo[]>(`${this.baseUrl}/search`, { params });
  }

  /**
   * Fetch all users of a given org (up to a limit).
   * Used by LOCAL mode to load data once and filter in-memory.
   *
   * Returns:
   * - Observable<UserInfo[]> containing users for that org.
   */
  all(orgId: number, limit = 50000): Observable<UserInfo[]> {
    const params = new HttpParams()
      .set('orgId', String(orgId))
      .set('limit', String(limit));

    return this.http.get<UserInfo[]>(`${this.baseUrl}/all`, { params });
  }
}
