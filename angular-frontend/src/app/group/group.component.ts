import { Component, OnInit } from '@angular/core';
import { Group } from './model/group.model';

@Component({
  selector: 'app-group',
  templateUrl: './group.component.html',
  styleUrls: ['./group.component.css']
})
export class GroupComponent implements OnInit{

  groups: Group[] = [
    new Group({"id": 1, "name": "Group 1", "description": "Description of Group 1", 
    "creationDate": new Date(2023, 4, 10, 21, 34, 12), "isSuspended": false, "suspendedReason": ""}),
    new Group({"id": 2, "name": "Group 2", "description": "Description of Group 2", 
    "creationDate": new Date(2023, 4, 10, 22, 12, 10), "isSuspended": false, "suspendedReason": ""})];
  
  constructor() {

  }

  ngOnInit(): void {

  }
}
