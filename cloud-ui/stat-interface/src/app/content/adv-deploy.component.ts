import { DefaultRatiosService } from '../_services/default-ratios.service';
import { RatiosService } from '../_services/ratios.service';
import { DefaultRatioPipe } from '../pipes/default-ratio.pipe';
import { RatioFromJsonPipe } from '../pipes/ratio-from-json.pipe';
import { RatioToJsonPipe } from '../pipes/ratio-to-json.pipe';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-adv-deploy',
  templateUrl: './adv-deploy.component.html',
  styleUrls: ['./adv-deploy.component.css'],
  providers: [ RatiosService, RatioFromJsonPipe, RatioToJsonPipe, DefaultRatioPipe, DefaultRatiosService ]
})
export class AdvDeployComponent implements OnInit {

  constructor() { }

  ngOnInit() {
  }

}
