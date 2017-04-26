import { DefaultRatiosService } from '../_services/default-ratios.service';
import { RatiosService } from '../_services/ratios.service';
import { Children } from '../models/models';
import { DefaultRatioPipe } from '../pipes/default-ratio.pipe';
import { RatioFromJsonPipe } from '../pipes/ratio-from-json.pipe';
import { RatioToJsonPipe, Ratio } from '../pipes/ratio-to-json.pipe';
import { Component, OnInit, ViewChild, OnChanges } from '@angular/core';
import { CompleterService, CompleterCmp, CompleterData } from 'ng2-completer';
import { Subject, Subscription } from 'rxjs';

@Component({
  selector: 'app-ratio',
  templateUrl: './ratio.component.html',
  styleUrls: ['./ratio.component.css']
})
export class RatioComponent implements OnInit, OnChanges {
  protected data: Children[] = [];
  protected nameToVersion = [];
  protected version = new Subject();
  protected nodeChildren: Children;

  protected dataService: CompleterData;
  protected dataServiceVersions: CompleterData;

  @ViewChild('serviceName') serviceName: CompleterCmp;
  @ViewChild('version') versionCmp: CompleterCmp;

  subscription: Subscription;

  isServiceSelected = false;

  protected value = 0;
  protected options = {
    unit: '%',
    bgColor: 'transparent',
    trackWidth: 45,
    barWidth: 30,
    barColor: '#187cff',
    trackColor: '#2C3E50',
    textColor: '#2C3E50'
  };

  constructor(private completerService: CompleterService,
    private _service: RatiosService,
    private _fromJson: RatioFromJsonPipe,
    private _toJson: RatioToJsonPipe,
    private _fallbackPipe: DefaultRatioPipe,
    private _fallbackService: DefaultRatiosService) { }

  ngOnInit() {
    this.createCookData();
    this.createVersions();
    this.dataService = this.completerService.local(this.data, 'name', 'name');
    this.dataServiceVersions = this.completerService.local(this.version);
  }

  ngOnChanges() {
    let names: string[] = this.nameToVersion[0];
    if (names.indexOf(this.serviceName.value) > -1) {
      let index = this.nameToVersion[0].indexOf(this.serviceName.value);
      let versions = this.nameToVersion[1];
      this.version.next(versions[index]);
    }
  }

  updateDial() {
    this.isServiceSelected = false;
    this.subscription = this._service.getRatios().subscribe(
      data => {
        if (data) {
          let versRatioArray = this._fromJson.transform(data);
          let versions: string[] = versRatioArray[0];
          let ratios: string[] = versRatioArray[1];
          if (versions.indexOf(this.versionCmp.value) > -1) {
            let index = versions.indexOf(this.versionCmp.value);
            this.value = parseInt(ratios[index], 10);
          } else {
            this.value = 0;
          }
          this.isServiceSelected = true;
        }
      },
      err => console.log(err)
    );
  }

  updateRatio() {
    if (confirm('Are you sure you wish to change this ratio?')) {
      let versions = [];
      versions.push('1.0.1');
      versions.push('1.0.2');

      let ratios = [];
      if (this.versionCmp.value === '1.0.1') {
        ratios.push(this.value);
        ratios.push(100 - this.value);
      } else if (this.versionCmp.value === '1.0.2') {
        ratios.push(100 - this.value);
        ratios.push(this.value);
      }

      let accuracy = ['PATCH', 'PATCH'];

      let args = [versions, ratios, accuracy];

      let ratioArray: Ratio[] = this._toJson.transform(args);
      this._service.setRatios(JSON.stringify(ratioArray)).subscribe();
    }
  }

  click() {
    if (this.serviceName.value) {
      let names: string[] = this.nameToVersion[0];
      let lcNames: string[] = [];
      for (let name of names) {
        lcNames.push(name.toLowerCase());
      }

      let localName: string = <string>this.serviceName.value;
      localName = localName.toLowerCase();
      if (lcNames.indexOf(localName) > -1) {
        let index = lcNames.indexOf(localName);
        let versions = this.nameToVersion[1];
        this.version.next(versions[index]);
      }
    }
  }

  private createVersions() {
    let names: string[] = [];
    names.push('AIRLINES', 'hello-world');
    let airlinesVersions: string[] = [];
    let helloWorldVersions: string[] = [];
    airlinesVersions.push('1.0.1', '1.0.2');
    helloWorldVersions.push('1.0.1', '1.0.2');
    let versions: string[][] = [];
    versions.push(airlinesVersions, helloWorldVersions);
    this.nameToVersion.push(names, versions);
  }

  createCookData() {
    let nodeChildren = [];
    nodeChildren.push(new Children('AIRLINES', 'MICROSERVICE', '1.0.1', '172.17.0.4', 'STARTING'));
    nodeChildren.push(new Children('hello-world', 'MICROSERVICE', '1.0.2', '172.17.0.3', 'DOWN'));
    this.data = nodeChildren;
  }

}
