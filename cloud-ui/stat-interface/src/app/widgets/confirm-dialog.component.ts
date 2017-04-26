import { Component, OnInit } from '@angular/core';
import { MdDialogRef } from '@angular/material';

@Component({
  selector: 'app-confirm-dialog',
  template: `
        <p>{{ title }}</p>
        <p>{{ message }}</p>
        <button type="button" md-raised-button 
            (click)="dialogRef.close(true)">OK</button>
        <button type="button" md-button 
            (click)="dialogRef.close()">Cancel</button>
    `,
  styleUrls: ['./confirm-dialog.component.css']
})
export class ConfirmDialogComponent {


  public title: string;
  public message: string;

  constructor(public dialogRef: MdDialogRef<ConfirmDialogComponent>) {

  }

}
