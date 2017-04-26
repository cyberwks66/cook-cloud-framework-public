import { Children } from '../models/models';
import { Component, OnInit, OnChanges, ViewChild } from '@angular/core';
import { CompleterData, CompleterService, CompleterCmp } from 'ng2-completer';
import { Subject } from 'rxjs';

@Component({
  selector: 'app-deploy',
  templateUrl: './deploy.component.html',
  styleUrls: ['./deploy.component.css']
})
export class DeployComponent implements OnInit, OnChanges {
  protected data: Children[] = [];
  protected nameToVersion = [];
  protected version = new Subject();
  protected nodeChildren: Children;

  protected dataService: CompleterData;
  protected dataServiceVersions: CompleterData;

  @ViewChild('serviceName') serviceName: CompleterCmp;
  @ViewChild('version') versionCmp: CompleterCmp;

  constructor(private completerService: CompleterService) { }

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

  deploy() {
    if (confirm('Are sure you you wish to deploy the selected Service/Version?')) {

    } else {

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
