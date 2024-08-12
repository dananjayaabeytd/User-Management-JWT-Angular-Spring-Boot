import { Injectable } from '@angular/core';
import { HttpClient,HttpHeaders } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class UsersService {

  private BASE_URL = "http://localhost:1010";

  constructor(private http: HttpClient) { }

}
