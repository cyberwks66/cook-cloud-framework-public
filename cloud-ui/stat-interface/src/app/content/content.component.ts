import { DefaultRatiosService } from '../_services/default-ratios.service';
import { RatiosService } from '../_services/ratios.service';
import { DefaultRatioPipe } from '../pipes/default-ratio.pipe';
import { DiscoveryData } from '../pipes/discovery-data.pipe';
import { RatioFromJsonPipe } from '../pipes/ratio-from-json.pipe';
import { RatioToJsonPipe } from '../pipes/ratio-to-json.pipe';
import { Component, OnInit } from '@angular/core';
import { Modal } from 'angular2-modal/plugins/bootstrap';

@Component({
  selector: 'app-content',
  templateUrl: './content.component.html',
  styleUrls: ['./content.component.css'],
  providers: [ DiscoveryData, RatiosService, RatioFromJsonPipe, RatioToJsonPipe, Modal, DefaultRatioPipe, DefaultRatiosService ]
})
export class ContentComponent implements OnInit {

  constructor() { }

  ngOnInit() {
  }

}
