import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';

// External imports
import { ChartsModule } from 'ng2-charts/ng2-charts';
import { nvD3 } from 'ng2-nvd3';
import { CollapseModule } from 'ng2-bootstrap';
import { Ng2CompleterModule } from 'ng2-completer';

// Local imports
import { AppComponent } from './app.component';
import { HomeComponent } from './home/home.component';
import { AboutComponent } from './about/about.component';
import { BottomComponentComponent } from './sub-comps/bottom-component/bottom-component.component';
import { TopLeftComponentComponent } from './sub-comps/top-left-component/top-left-component.component';
import { TopRightComponentComponent } from './sub-comps/top-right-component/top-right-component.component';
import { ContentComponent } from './content/content.component';
import { TopComponent } from './sub-comps/top/top.component';
import { SecondComponent } from './sub-comps/second/second.component';
import { ThirdComponent } from './sub-comps/third/third.component';
import { TreeGraphComponent } from './_graphs/tree-graph/tree-graph.component';
import { DialDirective } from './widgets/dial.directive';
import { DiscBarGraphDataPipe } from './pipes/disc-bar-graph-data.pipe';
import { RatioToJsonPipe } from './pipes/ratio-to-json.pipe';
import { RatioFromJsonPipe } from './pipes/ratio-from-json.pipe';
import { DefaultRatioPipe } from './pipes/default-ratio.pipe';
import { DeployComponent } from './adv-deploy/deploy.component';
import { RatioComponent } from './adv-deploy/ratio.component';
import { AdvDeployComponent } from './content/adv-deploy.component';
import { Routes, RouterModule } from '@angular/router';

const appRoutes: Routes = [
  {
    path: 'deployment', component: AdvDeployComponent
  },
  {
    path: 'graphs', component: ContentComponent
  }
];


@NgModule({
  declarations: [
    AppComponent,
    nvD3,
    HomeComponent,
    AboutComponent,
    TopLeftComponentComponent,
    TopRightComponentComponent,
    BottomComponentComponent,
    ContentComponent,
    TopComponent,
    SecondComponent,
    ThirdComponent,
    TreeGraphComponent,
    DialDirective,
    DiscBarGraphDataPipe,
    RatioToJsonPipe,
    RatioFromJsonPipe,
    DefaultRatioPipe,
    DeployComponent,
    RatioComponent,
    AdvDeployComponent,
  ],
  imports: [
    RouterModule.forRoot(appRoutes),
    BrowserModule,
    FormsModule,
    HttpModule,
    ChartsModule,
    CollapseModule,
    Ng2CompleterModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
