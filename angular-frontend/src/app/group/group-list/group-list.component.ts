import { Component, OnInit } from '@angular/core';
import { Group } from '../model/group.model';
import { GroupService } from '../services/group.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-group-list',
  templateUrl: './group-list.component.html',
  styleUrls: ['./group-list.component.css']
})
export class GroupListComponent implements OnInit{
  groups: Group[] = [];
  
  constructor(
    private groupService: GroupService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.groupService.getAll().subscribe(
      result => {
        this.groups = result.body as Group[];
      }
    );
  }
}
