import { DiscoveryStatsService } from '../../_services/discovery-stats.service';
import { DiscBarGraphDataPipe } from '../../pipes/disc-bar-graph-data.pipe';
import { Component, OnInit, ViewChild, OnDestroy } from '@angular/core';
import { Observable, Subscription } from 'rxjs';
import 'chart.js';

@Component({
  selector: 'app-second',
  templateUrl: './second.component.html',
  styleUrls: ['./second.component.css'],
  providers: [DiscBarGraphDataPipe, DiscoveryStatsService]
})
export class SecondComponent {

}
