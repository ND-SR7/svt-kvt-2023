import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddAddEditGroupComponent } from './add-edit-group.component';

describe('AddAddEditGroupComponent', () => {
  let component: AddAddEditGroupComponent;
  let fixture: ComponentFixture<AddAddEditGroupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AddAddEditGroupComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddAddEditGroupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
