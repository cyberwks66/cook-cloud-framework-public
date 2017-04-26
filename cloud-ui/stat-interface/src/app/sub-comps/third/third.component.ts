import { DefaultRatiosService } from '../../_services/default-ratios.service';
import { RatiosService } from '../../_services/ratios.service';
import { DefaultRatioPipe } from '../../pipes/default-ratio.pipe';
import { RatioFromJsonPipe } from '../../pipes/ratio-from-json.pipe';
import { RatioToJsonPipe, Ratio } from '../../pipes/ratio-to-json.pipe';
import { Component, OnInit, ViewContainerRef, ViewChild } from '@angular/core';
import { Overlay } from 'angular2-modal';
import { Modal } from 'angular2-modal/plugins/bootstrap';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-third',
  templateUrl: './third.component.html',
  styleUrls: ['./third.component.css']
})
export class ThirdComponent implements OnInit {
  private subscription: Subscription;
  protected lastElem = 1;
  protected blueValue = -1;
  protected greenValue = -1;
  protected blueOptions = {
    unit: '%',
    subText: {
      enabled: true,
      text: '1.0.1',
      color: '#708090',
      font: 'auto'
    },
    bgColor: 'transparent',
    trackWidth: 45,
    barWidth: 30,
    barColor: '#187cff',
    trackColor: '#2C3E50',
    textColor: '#2C3E50'
  };
  protected greenOptions = {
    unit: '%',
    subText: {
      enabled: true,
      text: '1.0.2',
      color: '#708090',
      font: 'auto'
    },
    bgColor: 'transparent',
    trackWidth: 45,
    barWidth: 30,
    barColor: '#47ff19',
    trackColor: '#2C3E50',
    textColor: '#2C3E50'
  };
  isDefault = false;

  constructor(
    private _service: RatiosService,
    private _fromJson: RatioFromJsonPipe,
    private _toJson: RatioToJsonPipe,
    private _fallbackPipe: DefaultRatioPipe,
    private _fallbackService: DefaultRatiosService) {
  }

  ngOnInit() {
    //    console.log('\n\n\nGETTING DIAL DATA\n\n\n');
    this.subscription = this._service.getRatios().subscribe(
      data => {
        if (data) {
          let versRatioArray = this._fromJson.transform(data);
          let versions: string[] = versRatioArray[0];
          let ratios: string[] = versRatioArray[1];
          this.blueValue = parseInt(ratios[0]);
          this.blueOptions = {
            unit: '%',
            subText: {
              enabled: true,
              text: versions[0],
              color: '#708090',
              font: 'auto'
            },
            bgColor: 'transparent',
            trackWidth: 45,
            barWidth: 30,
            barColor: '#187cff',
            trackColor: '#2C3E50',
            textColor: '#2C3E50'
          };
          this.greenValue = parseInt(ratios[1]);
          this.greenOptions = {
            unit: '%',
            subText: {
              enabled: true,
              text: versions[1],
              color: '#708090',
              font: 'auto'
            },
            bgColor: 'transparent',
            trackWidth: 45,
            barWidth: 30,
            barColor: '#47ff19',
            trackColor: '#2C3E50',
            textColor: '#2C3E50'
          };
        } else {
          this.isDefault = true;
        }
      }
    );
    if (this.isDefault) {
      this._fallbackService.getDefaultRatios().subscribe(
        data => {
          let versRatioArray = this._fallbackPipe.transform(data, 'AIRLINES');
          let versions: string[] = versRatioArray[0];
          let ratios: string[] = versRatioArray[1];
          this.blueValue = 50;
          this.blueOptions = {
            unit: '%',
            subText: {
              enabled: true,
              text: versions[0],
              color: '#708090',
              font: 'auto'
            },
            bgColor: 'transparent',
            trackWidth: 45,
            barWidth: 30,
            barColor: '#187cff',
            trackColor: '#2C3E50',
            textColor: '#2C3E50'
          };
          this.greenValue = 50;
          this.greenOptions = {
            unit: '%',
            subText: {
              enabled: true,
              text: versions[1],
              color: '#708090',
              font: 'auto'
            },
            bgColor: 'transparent',
            trackWidth: 45,
            barWidth: 30,
            barColor: '#47ff19',
            trackColor: '#2C3E50',
            textColor: '#2C3E50'
          };
        },
        err => console.log(err)
      );
    }
  }

  update() {
    if (this.lastElem === 1) {
      this.greenValue = 100 - this.blueValue;
    } else if (this.lastElem === 2) {
      this.blueValue = 100 - this.greenValue;
    }
  }

  confirm() {
    if (confirm('Are you sure you wish to change the ratios?')) {
      let versions = [];
      versions.push(this.blueOptions.subText.text);
      versions.push(this.greenOptions.subText.text);

      let ratios = [];
      ratios.push(this.blueValue);
      ratios.push(this.greenValue);

      let accuracy = ['PATCH', 'PATCH'];

      let args = [versions, ratios, accuracy];

      let ratioArray: Ratio[] = this._toJson.transform(args);
      this._service.setRatios(JSON.stringify(ratioArray)).subscribe();
    }
  }

  click(clickElem) {
    this.lastElem = clickElem;
    this.update();
  }

}
