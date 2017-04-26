import { Injectable } from '@angular/core';
import { Http, Headers, Response, RequestOptions, RequestMethod } from '@angular/http';
import { Observable } from 'rxjs';
import 'rxjs';

@Injectable()
export class RatiosService {
  private headers = new Headers({ 'Content-type': 'application/json', 'Accept': 'application/json'});
  private options = new RequestOptions({ headers: this.headers, method: 'POST' });
  private url = 'http://edge.platform.cooksys.com:8081/routes/airlines/ratios';
  private postURL = 'http://localhost:4500/ratios';
  constructor(private _http: Http) { }

  getRatios() {
    return this._http.get(this.url, this.headers).map(res => res.json());
  }

  setRatios(body) {
    return this._http.post(this.postURL, body, { headers: this.headers }).map(res => res.json());
  }

  private extractData(res: Response) {
    let body = res.json();
    return body.data || {};
  }

}
