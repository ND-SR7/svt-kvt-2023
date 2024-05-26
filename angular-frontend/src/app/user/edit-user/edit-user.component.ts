import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserService } from '../services/user.service';
import { Router } from '@angular/router';
import { User } from '../model/user.model';
import { JwtHelperService } from '@auth0/angular-jwt';
import { Image } from 'src/app/post/model/image.model';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-edit-user',
  templateUrl: './edit-user.component.html',
  styleUrls: ['./edit-user.component.css']
})
export class EditUserComponent implements OnInit {

  form: FormGroup;
  imagePath: string = '';

  user: User = new User();
  image: Image = new Image();

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private toastr: ToastrService,
    private router: Router
  ) {
    this.form = this.fb.group({
      displayName: [null, Validators.minLength(3)],
      description: [null, Validators.maxLength(100)],
      image: [null, Validators.nullValidator]
    });
  }

  ngOnInit(): void {
    const item: string = localStorage.getItem('user') || '';
    const jwt: JwtHelperService = new JwtHelperService();
    const sub: string = jwt.decodeToken(item).sub;

    this.userService.getOneByUsername(sub).subscribe(
      result => {
        this.user = result.body as User;
        this.form.patchValue(this.user);
      }
    );
  }

  onFileChange(event: Event): void {
    const fileInput = event.target as HTMLInputElement;
    const file = fileInput.files?.[0];
    if (file) {
      this.imagePath = file.name;
    }
  }

  submit(): void {
    if (this.form.value.displayName != '')
      this.user.displayName = this.form.value.displayName;
    if (this.form.value.description != '')
      this.user.description = this.form.value.description;
    if (this.imagePath != '') {
      this.image.path = '../../assets/images/' + this.imagePath;
      this.image.belongsToUserId = this.user.id;
      this.user.profileImage = this.image;
    }

    this.userService.updateUser(this.user).subscribe(
      result => {
        this.toastr.success('Successfully edited your profile');
        this.router.navigate(['/users/profile']);
      },
      error => {
        this.toastr.error('Error while editing your profile');
        console.log(error);
      }
    );
  }
}
