import { Injectable } from '@angular/core';
import { Http, Response, Headers } from '@angular/http';

@Injectable()
export class DefaultRatiosService {
  private headers = new Headers({ 'Content-type': 'application/json', 'Accept': 'application/json' });
  private host = 'http://test01.platform.cooksys.com:8761/graph';
  constructor(private _http: Http) { }

  getDefaultRatios() {
    return this._http.get(this.host, this.headers)
      .map((res: Response) => {
        return res.json();
      });
  }

}
