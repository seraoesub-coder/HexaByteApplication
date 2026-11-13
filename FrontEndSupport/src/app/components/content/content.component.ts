import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-content',
  templateUrl: './content.component.html',
  styleUrls: ['./content.component.css'],
})
export class ContentComponent {
  @Input() title: string;

  constructor() {
    this.title = ' Hello World';
  }

  currentTime: string = '';
  isFullScreen: boolean = false;

  ngOnInit(): void {
    this.updateTime(); // Call once on init
    setInterval(() => this.updateTime(), 1000); // Update every second
  }

  updateTime(): void {
    const now = new Date();
    this.currentTime = now.toLocaleString(); // Or use toLocaleTimeString() / toISOString() as needed
  }
}

