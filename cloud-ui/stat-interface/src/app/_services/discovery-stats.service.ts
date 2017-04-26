import { DiscoveryData } from '../pipes/discovery-data.pipe';
import { Injectable } from '@angular/core';
import { Http, Headers, Response } from '@angular/http';
import { Observable } from 'rxjs';

@Injectable()
export class DiscoveryStatsService {
  private headers = new Headers({ 'Content-type': 'application/json', 'Accept': 'application/json' });
  private host = 'http://test01.platform.cooksys.com:8761/graph';
  constructor(private _http: Http, private _pipe: DiscoveryData) { }
  getDiscoveryData() {
    return this._http.get(this.host, this.headers)
      .map((res: Response) => {
        return res.json();
      });
  }
}
