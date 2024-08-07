import { Component, OnInit } from '@angular/core';
import { Group } from '../model/group.model';
import { GroupService } from '../services/group.service';
import { User } from 'src/app/user/model/user.model';
import { JwtHelperService } from '@auth0/angular-jwt';
import { UserService } from 'src/app/user/services/user.service';
import { GroupRequest } from '../model/groupRequest.model';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-group-list',
  templateUrl: './group-list.component.html',
  styleUrls: ['./group-list.component.css']
})
export class GroupListComponent implements OnInit {
  groups: Group[] = [];
  user: User = new User();
  isAdmin: boolean = false;

  groupRequests: Map<number, GroupRequest[]> = new Map(); //id grupe i njeni zahtevi
  userGroups: Group[] = []; //grupe kojih je ulogovani korisnik clan

  constructor(
    private groupService: GroupService,
    private userService: UserService,
    private toastr: ToastrService
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

    let sub, role: string;
    const item = localStorage.getItem('user') || '';

    const jwt: JwtHelperService = new JwtHelperService();
		const decodedToken = jwt.decodeToken(item);
    sub = decodedToken.sub;
    role = decodedToken.role.authority;

    if (role.includes('ADMIN'))
      this.isAdmin = true;

    this.userService.getOneByUsername(sub).subscribe(
      result => {
        this.user = result.body as User;

        this.userService.getUserGroups(this.user.id).subscribe(
          result => {
            this.userGroups = result.body as Group[];
          }
        );
      }
    );
  }

  canAccess(groupId: number): boolean {
    let access: boolean = false;
    
    this.userGroups.forEach(group => {
      if (group.id == groupId)
        access = true;
    });

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

    if (this.canAccess(groupId))
      canSend = false;

    return canSend;
  }

  sendRequest(groupId: number): void {
    const groupRequest: GroupRequest = new GroupRequest();
    groupRequest.createdAt = new Date().toISOString().slice(0, -1);
    groupRequest.createdByUserId = this.user.id;
    groupRequest.forGroupId = groupId;

    this.groupService.sendGroupRequest(groupRequest).subscribe(
      result => {
        this.toastr.success('Successfully sent request to join group');
        location.reload();
      },
      error => {
        this.toastr.error('Error while sending group request');
        console.log(error);
      }
    );
  }

  downloadRules(filename: string):void {
    this.groupService.downloadFile(filename).subscribe(
      result => {
        const url = window.URL.createObjectURL(result);
        const a = document.createElement('a');
        a.href = url;
        a.download = filename;
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        a.remove();
      },
      error => {
        this.toastr.error('Error while fetching file for download');
        console.log(error);
      }
    );
  }
}
