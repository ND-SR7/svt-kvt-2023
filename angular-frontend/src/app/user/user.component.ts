import { Component, OnInit } from '@angular/core';
import { User } from './model/user.model';
import { UserService } from './services/user.service';
import { Router } from '@angular/router';
import { JwtHelperService } from '@auth0/angular-jwt';
import { Image } from '../post/model/image.model';
import { Group } from '../group/model/group.model';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit {

  user: User = new User();
  image: Image = new Image();
  groups: Group[] = [];

  constructor(
    private userService: UserService,
    private toastr: ToastrService
  ) { }
  
  ngOnInit(): void {
    const item: string = localStorage.getItem('user') || '';
    const jwt: JwtHelperService = new JwtHelperService();
    const sub: string = jwt.decodeToken(item).sub;

    this.userService.getOneByUsername(sub).subscribe(
      result => {
        this.user = result.body as User;

        this.userService.getProfileImage(this.user.id).subscribe(
          result => {
            if (result.body != null)
              this.image = result.body as Image;
          },
          error => {
            this.toastr.error('Error while retriving profile image');
            console.log(error);
          }
        );

        this.userService.getUserGroups(this.user.id).subscribe(
          result => {
            this.groups = result.body as Group[];
          },
          error => {
            this.toastr.error('Error while retriving groups user is member of');
            console.log(error);
          }
        );
      },
      error => {
        this.toastr.error('Error while retriving profile info');
        console.log(error);
      }
    );
  }
}
