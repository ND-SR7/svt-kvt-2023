import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Group } from "src/app/group/model/group.model";
import { User } from 'src/app/user/model/user.model';
import { UserService } from 'src/app/user/services/user.service';
import { GroupService } from '../services/group.service';
import { JwtHelperService } from '@auth0/angular-jwt';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-add-edit-group',
  templateUrl: './add-edit-group.component.html',
  styleUrls: ['./add-edit-group.component.css']
})
export class AddEditGroupComponent implements OnInit {

  form: FormGroup;
  editing: boolean = this.router.url.includes('edit');
  fileError: boolean = false;

  group: Group = new Group();
  user: User = new User();

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private groupService: GroupService,
    private toastr: ToastrService,
    private router: Router
  ) { 
    this.form = this.fb.group({
      name: [null, Validators.required],
      description: [null, Validators.required],
      rules: [null, Validators.required],
      descriptionFile: [null, Validators.required]
    });

    if (this.editing) {
      const id: number = Number.parseInt(this.router.url.split('/')[3]);
      this.groupService.getOne(id).subscribe(
        result => {
          this.group = result.body as Group;
          this.form.patchValue(this.group);
        },
        error => {
          toastr.error('An error occurred retriving group!');
          console.log(error);
        }
      );
    }
   }

   ngOnInit(): void {
    const jwt: JwtHelperService = new JwtHelperService();
    const userToken: string = localStorage.getItem('user') || '';
    const decoded = jwt.decodeToken(userToken);

    this.userService.getOneByUsername(decoded.sub).subscribe(
      result => {
        this.user = result.body as User;
      },
      error => {
        this.toastr.warning('Couln\'t find logged in user');
        console.log(error);
      }
    );
   }

   onFileChange(event: any) {
    const file = event.target.files[0];
    if (file && file.type === 'application/pdf') {
      this.form.patchValue({
        descriptionFile: file
      });
      this.fileError = false;
    } else {
      this.form.patchValue({
        descriptionFile: null
      });
      this.fileError = true;
    }
  }

   submit(): void {
    if (!this.editing) {
      if (!this.form.valid) {
        if (!this.form.controls['descriptionFile'].value) {
          this.fileError = true;
        }
        return;
      }

      let group: Group = new Group();
      group.name = this.form.value.name;
      group.description = this.form.value.description;
      group.creationDate = new Date().toISOString().slice(0, -1);
      group.suspended = false;
      group.rules = this.form.value.rules;

      const formData: FormData = new FormData();
      formData.append('file', this.form.value.descriptionFile);
      formData.append('group', new Blob([JSON.stringify(group)], { type: 'application/json' }));

      this.groupService.add(formData).subscribe(
        result => {
          group = (JSON.parse(result)) as Group;
          
          this.groupService.addGroupAdmin(group.id, this.user.id).subscribe(
            result => {
              this.toastr.success('Successfully created a group');
              this.router.navigate(['/groups']);
            },
            error => {
              this.toastr.error('Error while adding group\'s admin');
              console.log(error);
            }
          );
        },
        error => {
          this.toastr.error('Error while creating group');
          console.log(error);
        }
      );
    } else {
      this.group.name = this.form.value.name;
      this.group.description = this.form.value.description;
      this.group.rules = this.form.value.rules;

      this.groupService.edit(this.group).subscribe(
        result => {
          this.toastr.success('Successfully edited the group');
          this.router.navigate(['/groups/' + this.group.id]);
        },
        error => {
          this.toastr.error('An error occurred editing the group');
          console.log(error);
        }
      );
    }
  }
}
