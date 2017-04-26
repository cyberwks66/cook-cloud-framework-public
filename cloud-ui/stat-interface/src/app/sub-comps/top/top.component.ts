import { TreeGraphComponent } from '../../_graphs/tree-graph/tree-graph.component';
import { DiscoveryStatsService } from '../../_services/discovery-stats.service';
import { Children } from '../../models/models';
import { DiscoveryData } from '../../pipes/discovery-data.pipe';
import { Component, OnInit, ViewEncapsulation, OnChanges, ViewChild } from '@angular/core';
import { CompleterData, CompleterService, CompleterCmp } from 'ng2-completer';
import { Subject } from 'rxjs';

@Component({
  selector: 'app-top',
  templateUrl: './top.component.html',
  styleUrls: ['./top.component.css'],
  encapsulation: ViewEncapsulation.None,
  providers: [DiscoveryStatsService, DiscoveryData]
})
export class TopComponent implements OnInit {
  @ViewChild('serviceName') serviceName: CompleterCmp;
  @ViewChild('version') versionCmp: CompleterCmp;
  public isCollapsed = true;
  public graphRendered = false;
  public buttonText = 'Open';
  protected dataService: CompleterData;
  protected dataServiceVersions: CompleterData;
  protected data: Children[] = [];
  protected nameToVersion = [];
  protected version = new Subject();
  protected nodeChildren: Children;
  public jsonTest;
  constructor(private completerService: CompleterService,
    private _discoveryData: DiscoveryStatsService,
    private _discoveryPipe: DiscoveryData) { }

  ngOnInit() {
    this.createCookData();
    this.createVersions();
    this.dataService = this.completerService.local(this.data, 'name', 'name');
    this.dataServiceVersions = this.completerService.local(this.version);
  }

  click() {
    let names: string[] = this.nameToVersion[0];
    if (names.indexOf(this.serviceName.value) > -1) {
      console.log('inside if statement');
      let index = this.nameToVersion[0].indexOf(this.serviceName.value);
      let versions = this.nameToVersion[1];
      console.log('index == ' + index);
      for (let v of versions[index]) {
        console.log(v);
      }
      this.version.next(versions[index]);
    } else {
      console.log('outside if statement');
    }
  }

  private createVersions() {
    let names: string[] = [];
    names.push('AIRLINES', 'hello-world');
    let airlinesVersions: string[] = [];
    let helloWorldVersions: string[] = [];
    airlinesVersions.push('1.0.1', '1.0.2', '1.0.3');
    helloWorldVersions.push('1.0.1', '1.0.2');
    let versions: string[][] = [];
    versions.push(airlinesVersions, helloWorldVersions);
    this.nameToVersion.push(names, versions);
  }

  public collapsed(event: any): void {
    this.graphRendered = false;
    this.buttonText = 'Open';
  }

  public expanded(event: any): void {
    this.graphRendered = true;
    this.buttonText = 'Close';
  }

  createCookData() {
    let nodeChildren = [];
    nodeChildren.push(new Children('AIRLINES', 'MICROSERVICE', '1.0.1', '172.17.0.4', 'STARTING'));
    nodeChildren.push(new Children('hello-world', 'MICROSERVICE', '1.0.2', '172.17.0.3', 'DOWN'));
    this.data = nodeChildren;
  }

}
