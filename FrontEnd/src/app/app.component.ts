import {Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})

export class AppComponent implements OnInit {
  title = 'HexaByte Reclamation Space';
  currentTime = new Date().toLocaleTimeString();

  ngOnInit() {
    // Simre un léger délai pour l'effet visuel du logo, puis fait un fondu de disparition
    setTimeout(() => {
      const loader = document.getElementById('initial-loader');
      if (loader) {
        loader.style.opacity = '0';
        setTimeout(() => loader.remove(), 500); // Supprime l'élément après l'animation
      }
    }, 500000); // Le logo reste affiché pendant 5 secondes
  }
}