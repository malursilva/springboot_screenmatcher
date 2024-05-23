import getDados from "./getDados.js";

const params = new URLSearchParams(window.location.search);
const serieId = params.get('id');
const listaTemporadas = document.getElementById('temporadas-select');
const fichaSerie = document.getElementById('temporadas-episodios');
const fichaDescricao = document.getElementById('ficha-descricao');

// Função para carregar temporadas
function carregarTemporadas() {
    getDados(`/series/${serieId}/season/all`)
        .then(data => {
            const temporadasUnicas = [...new Set(data.map(season => season.seasonNumber))];
            listaTemporadas.innerHTML = ''; // Limpa as opções existentes

            const optionDefault = document.createElement('option');
            optionDefault.value = '';
            optionDefault.textContent = 'Select season'
            listaTemporadas.appendChild(optionDefault);

            temporadasUnicas.forEach(season => {
                const option = document.createElement('option');
                option.value = season;
                option.textContent = season;
                listaTemporadas.appendChild(option);
            });

            const optionTodos = document.createElement('option');
            optionTodos.value = 'all';
            optionTodos.textContent = 'All seasons'
            listaTemporadas.appendChild(optionTodos);

            const optionTop5 = document.createElement('option');
            optionTop5.value = 'top5';
            optionTop5.textContent = 'Top 5 episodes'
            listaTemporadas.appendChild(optionTop5);
        })
        .catch(error => {
            console.error('Error on fetching seasons:', error);
        });
}

// Função para carregar episódios de uma temporada
function carregarEpisodios() {
    getDados(`/series/${serieId}/season/${listaTemporadas.value}`)
        .then(data => {
            const seasonSelected = listaTemporadas.value;
            const temporadasUnicas = [...new Set(data.map(season => season.seasonNumber))];
            fichaSerie.innerHTML = '';
            if (seasonSelected === 'top5') {
                const ul = document.createElement('ul');
                ul.className = 'episodios-lista';
                const listaHTML = data.map(episode => `
                        <li>
                            Season: ${episode.seasonNumber} | Episode: ${episode.number} : ${episode.title} &emsp;|&emsp; Rating: ${episode.rating}
                        </li>
                    `).join('');
                ul.innerHTML = listaHTML;
                
                const linha = document.createElement('br');
                fichaSerie.appendChild(linha);
                fichaSerie.appendChild(ul);
            } else {
                temporadasUnicas.forEach(season => {
                    const ul = document.createElement('ul');
                    ul.className = 'episodios-lista';

                    const episodiosTemporadaAtual = data.filter(episode => episode.seasonNumber === season);

                    const listaHTML = episodiosTemporadaAtual.map(episode => `
                        <li>
                            ${episode.number} - ${episode.title}
                        </li>
                    `).join('');
                    ul.innerHTML = listaHTML;

                    const paragrafo = document.createElement('p');
                    const linha = document.createElement('br');
                    paragrafo.textContent = `Season ${season}`;
                    fichaSerie.appendChild(paragrafo);
                    fichaSerie.appendChild(linha);
                    fichaSerie.appendChild(ul);
                    fichaSerie.appendChild(document.createElement('br'));
                    fichaSerie.appendChild(document.createElement('br'));
                });
            }
        })
        .catch(error => {
            console.error('Error on fetching episodes:', error);
        });
}

// Função para carregar informações da série
function carregarInfoSerie() {
    getDados(`/series/${serieId}`)
        .then(data => {
            fichaDescricao.innerHTML = `
                <img src="${data.posterUrl}" alt="${data.title}" />
                <div>
                    <h2>${data.title}</h2>
                    <div class="descricao-texto">
                        <p><b>Rating:</b> ${data.rating}</p>
                        <p>${data.synopsis}</p>
                        <p><b>Starring:</b> ${data.mainActors}</p>
                    </div>
                </div>
            `;
        })
        .catch(error => {
            console.error('Erro ao obter informações da série:', error);
        });
}

// Adiciona ouvinte de evento para o elemento select
listaTemporadas.addEventListener('change', carregarEpisodios);

// Carrega as informações da série e as temporadas quando a página carrega
carregarInfoSerie();
carregarTemporadas();
