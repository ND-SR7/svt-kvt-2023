import { Component, OnInit } from '@angular/core';
import { Group } from '../model/group.model';
import { GroupService } from '../services/group.service';
import { User } from 'src/app/user/model/user.model';
import { JwtHelperService } from '@auth0/angular-jwt';
import { UserService } from 'src/app/user/services/user.service';
import { GroupRequest } from '../model/groupRequest.model';

@Component({
  selector: 'app-group-list',
  templateUrl: './group-list.component.html',
  styleUrls: ['./group-list.component.css']
})
export class GroupListComponent implements OnInit {
  groups: Group[] = [];
  user: User = new User();

  groupRequests: Map<number, GroupRequest[]> = new Map(); //id grupe i njeni zahtevi

  constructor(
    private groupService: GroupService,
    private userService: UserService
  ) { }

  ngOnInit(): void {
    this.groupService.getAll().subscribe(
      result => {
        this.groups = result.body as Group[];

        this.groups.forEach(group => {
          this.groupService.getGroupRequests(group.id).subscribe(
            result => {
              this.groupRequests.set(group.id, result.body as GroupRequest[]);
            }
          );
        });
      }
    );

    let sub: string;
    const item = localStorage.getItem('user') || '';

    const jwt: JwtHelperService = new JwtHelperService();
		const decodedToken = jwt.decodeToken(item);
    sub = decodedToken.sub;

    this.userService.getOneByUsername(sub).subscribe(
      result => {
        this.user = result.body as User;
      }
    );
  }

  canAccess(groupId: number): boolean {
    let access: boolean = false;
    this.groupService.checkUserInGroup(groupId).subscribe(
      result => {
        if (result.body == true)
          access = true;
      }
    );
    return access;
  }

  canSendRequest(groupId: number): boolean {
    let canSend: boolean = true;
    this.groupRequests.forEach(requests => {
      requests.forEach(request => {
        if (request.createdByUserId == this.user.id && request.forGroupId == groupId)
          canSend = false;
      });
    });
    return canSend;
  }

  sendRequest(groupId: number): void {
    const groupRequest: GroupRequest = new GroupRequest();
    groupRequest.createdAt = new Date().toISOString().slice(0, -1);
    groupRequest.createdByUserId = this.user.id;
    groupRequest.forGroupId = groupId;

    this.groupService.sendGroupRequest(groupRequest).subscribe(
      result => {
        window.alert('Successfully sent request to join group');
        location.reload();
      },
      error => {
        window.alert('Error while sending group request');
        console.log(error);
      }
    );
  }
}
